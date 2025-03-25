package com.praveensk.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String billingServiceAddress, @Value("${billing.service.grpc.port:9001}") int billingServicePort) {
        log.info("Connecting to Billing service GRPC service at {}:{} ", billingServiceAddress, billingServicePort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(billingServiceAddress, billingServicePort).usePlaintext().build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse craeteBilling(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder().setPatientId(patientId).setName(name).setEmail(email).build();
        BillingResponse response = blockingStub.createBillingAccount(request);

        log.info("Received response from billing service via GRPC: {}", response);
        return response;
    }
}
