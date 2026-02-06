package com.optimus.rest;

import com.optimus.enums.ErrorCode;
import com.optimus.exception.ServiceException;
import com.optimus.vo.ApiInfo;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static com.optimus.constant.Constants.APP_INFO;

public abstract class BaseController {

    protected ApiInfo getAppInfo() {
        Object appInfo = RequestContextHolder.currentRequestAttributes().getAttribute(APP_INFO, RequestAttributes.SCOPE_REQUEST);
        if (!ObjectUtils.isEmpty(appInfo)) {
            return (ApiInfo) appInfo;
        }
        throw new ServiceException(ErrorCode.DATA_NOT_EXIST);
    }


}
