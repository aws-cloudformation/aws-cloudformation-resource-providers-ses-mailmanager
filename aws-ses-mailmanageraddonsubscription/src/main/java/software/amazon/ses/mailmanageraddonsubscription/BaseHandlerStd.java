package software.amazon.ses.mailmanageraddonsubscription;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.mailmanager.MailManagerClient;
import software.amazon.awssdk.services.mailmanager.model.ConflictException;
import software.amazon.awssdk.services.mailmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.mailmanager.model.ServiceQuotaExceededException;
import software.amazon.awssdk.services.mailmanager.model.ValidationException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {
  private final Map<Class<? extends Exception>, HandlerErrorCode> exceptionMap = Map.of(
          IllegalArgumentException.class, HandlerErrorCode.InvalidRequest,
          ValidationException.class, HandlerErrorCode.InvalidRequest,
          ConflictException.class, HandlerErrorCode.ResourceConflict,
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
