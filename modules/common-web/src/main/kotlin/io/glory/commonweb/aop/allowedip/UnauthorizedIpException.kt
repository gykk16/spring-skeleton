package io.glory.commonweb.aop.allowedip

import io.glory.common.codes.response.ErrorCode
import io.glory.common.exceptions.BizRuntimeException

class UnauthorizedIpException(
    clientIp: String,
) : BizRuntimeException(ErrorCode.UNAUTHORIZED_IP, clientIp)