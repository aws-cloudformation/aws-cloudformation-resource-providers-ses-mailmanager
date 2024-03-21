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
import java.util.Map;

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {
  public static final int MAX_TEMPLATE_NAME_LENGTH = 64;
  protected static final Constant STABILIZATION_DELAY_CREATE =
          Constant.of().timeout(Duration.ofMinutes(10L)).delay(Duration.ofSeconds(5L)).build();

  protected static final Constant STABILIZATION_DELAY_UPDATE =
          Constant.of().timeout(Duration.ofMinutes(5L)).delay(Duration.ofSeconds(5L)).build();

  protected static final Constant STABILIZATION_DELAY_DELETE =
          Constant.of().timeout(Duration.ofMinutes(5L)).delay(Duration.ofSeconds(5L)).build();

  private final Map<Class<? extends Exception>, HandlerErrorCode> exceptionMap = Map.of(
          IllegalArgumentException.class, HandlerErrorCode.InvalidRequest,
          ValidationException.class, HandlerErrorCode.InvalidRequest,
          ConflictException.class, HandlerErrorCode.AlreadyExists,
          ResourceNotFoundException.class, HandlerErrorCode.NotFound,
          ServiceQuotaExceededException.class, HandlerErrorCode.ServiceLimitExceeded,
          AwsServiceException.class, HandlerErrorCode.GeneralServiceException
  );

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
    HandlerErrorCode errorCode = exceptionMap.getOrDefault(exception.getClass(), HandlerErrorCode.Unknown);

    logger.log(String.format("[ClientRequestToken: %s]: <%s>", clientRequestToken, exceptionMessage));

    return ProgressEvent.failed(
            model,
            callbackContext,
            errorCode,
            exceptionMessage
    );
  }
}
