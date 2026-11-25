package com.example.demo.ServiceImpl;

import com.example.demo.AtmDisputePortType;
import com.example.demo.AtmDisputePortTypeService;
import jakarta.xml.ws.BindingProvider;

import java.net.URL;

public class NapasSoapClient {
    public AtmDisputePortType createPort() throws Exception {

        // Đọc WSDL từ resources của project
        URL wsdlUrl = getClass()
                .getClassLoader()
                .getResource("wsdl/atm_pos_uat.wsdl");

        if (wsdlUrl == null) {
            throw new IllegalStateException(
                    "Không tìm thấy wsdl/atm_pos_uat.wsdl"
            );
        }

        AtmDisputePortTypeService service =
                new AtmDisputePortTypeService(wsdlUrl);

        AtmDisputePortType port =
                service.getAtmDisputePortTypeSoap11();

        BindingProvider bindingProvider =
                (BindingProvider) port;

        // Đặt endpoint thực tế của NAPAS
        bindingProvider.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            "http://localhost:8080/spring-security-demo.xml"
        );

        return port;
    }
}
