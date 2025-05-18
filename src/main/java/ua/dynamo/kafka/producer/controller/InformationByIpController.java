package ua.dynamo.kafka.producer.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import ua.dynamo.kafka.producer.model.SummaryInfo;
import ua.dynamo.kafka.producer.service.SummaryManagerService;

public class InformationByIpController {

    private final SummaryManagerService summaryManagerService;

    public InformationByIpController(SummaryManagerService summaryManagerService) {
        this.summaryManagerService = summaryManagerService;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/get-info", this::handleGetInfo);
    }

    private void handleGetInfo(Context ctx) {
        String ip = ctx.header("X-Forwarded-For");
        SummaryInfo summaryInfo = summaryManagerService.getSummaryInfo(ip);

        ctx.result(summaryInfo.toString());
    }
}
