package software.amazon.ses.mailmanageringresspoint;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.mailmanager.model.ConflictException;
import software.amazon.awssdk.services.mailmanager.model.ValidationException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.mailmanager.model.ServiceQuotaExceededException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.delay.Constant;

import java.time.Duration;

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {
  protected static final Constant STABILIZATION_DELAY_CREATE =
          Constant.of().timeout(Duration.ofMinutes(10L)).delay(Duration.ofSeconds(5L)).build();

  protected static final Constant STABILIZATION_DELAY_UPDATE =
          Constant.of().timeout(Duration.ofMinutes(5L)).delay(Duration.ofSeconds(5L)).build();

  protected static final Constant STABILIZATION_DELAY_DELETE =
          Constant.of().timeout(Duration.ofMinutes(5L)).delay(Duration.ofSeconds(5L)).build();

  @Override
  public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<ResourceModel> request,
          final CallbackContext callbackContext,
          final Logger logger) {
    return handleRequest(
            proxy,
            request,
            callbackContext != null ? callbackContext : new CallbackContext(),
            proxy.newProxy(ClientBuilder::getClient),
            logger
    );
  }

  protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<ResourceModel> request,
          final CallbackContext callbackContext,
          final ProxyClient<MailManagerClient> proxyClient,
          final Logger logger);

  protected ProgressEvent<ResourceModel, CallbackContext> handleError(
          final Exception exception,
          final ResourceModel model,
          final CallbackContext callbackContext,
          final Logger logger,
          final String clientRequestToken
  ) {

    final String exceptionMessage = exception.getMessage();

    if (exception instanceof IllegalArgumentException || exception instanceof ValidationException) {
      logger.log(String.format("[ClientRequestToken: %s] Property validation failure for MailManager IngressPoint API: %s", clientRequestToken, exceptionMessage));
      return ProgressEvent.failed(
              model,
              callbackContext,
              HandlerErrorCode.InvalidRequest,
              exceptionMessage
      );
    }

    if (exception instanceof ConflictException) {
      logger.log(String.format("[ClientRequestToken: %s] MailManager IngressPoint resource has conflict exception: %s", clientRequestToken, exceptionMessage));
      return ProgressEvent.failed(
              model,
              callbackContext,
              HandlerErrorCode.AlreadyExists,
              exceptionMessage
      );
    }

    if (exception instanceof ResourceNotFoundException) {
      logger.log(String.format("[ClientRequestToken: %s] MailManager IngressPoint resource does not exist: %s", clientRequestToken, exceptionMessage));
      return ProgressEvent.failed(
              model,
              callbackContext,
              HandlerErrorCode.NotFound,
              exceptionMessage
      );
    }

    if (exception instanceof ServiceQuotaExceededException) {
      logger.log(String.format("[ClientRequestToken: %s] MailManager IngressPoint API failed: %s", clientRequestToken, exceptionMessage));
      return ProgressEvent.failed(
              model,
              callbackContext,
              HandlerErrorCode.ServiceLimitExceeded,
              exceptionMessage
      );
    }

    if (exception instanceof AwsServiceException) {
      logger.log(String.format("[ClientRequestToken: %s] MailManager IngressPoint Service Error: %s", clientRequestToken, exceptionMessage));
      return ProgressEvent.failed(
              model,
              callbackContext,
              HandlerErrorCode.GeneralServiceException,
              exceptionMessage
      );
    }

    logger.log(String.format("[ClientRequestToken: %s] IngressPoint API has unsupported exception: %s", clientRequestToken, exceptionMessage));
    return ProgressEvent.failed(
            model,
            callbackContext,
            HandlerErrorCode.Unknown,
            exceptionMessage
    );
  }
}
