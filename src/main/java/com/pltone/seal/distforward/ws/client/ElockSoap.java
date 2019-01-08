package com.pltone.seal.distforward.ws.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * 物流配送接口
 *
 * @author chenlong
 * @version 1.0 2018-01-19
 */
@WebService(name = "ElockSoap", targetNamespace = "http://www.cnpc.com/")
public interface ElockSoap {
    /**
     * 配送函数
     *
     * @param txt xml文本
     * @return 接口调用结果
     */
    @WebMethod(operationName = "SetPlan")
    @WebResult(name = "SetPlanResult", targetNamespace = "http://www.cnpc.com/")
    String setPlan(@WebParam(name = "txt", targetNamespace = "http://www.cnpc.com/") String txt);
}