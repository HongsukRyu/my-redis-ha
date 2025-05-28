package com.backend.api.common.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;

public class CustomErrorReportValveHandler extends ErrorReportValve {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorReportValveHandler.class);

    /**
     * Tomcat 기본 에러 페이지 커스텀
     *
     */
    @Override
    protected void report(Request request, Response response, Throwable t) {
        logger.debug("CustomErrorReportValveHandler!");

        try {

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
            out.close();

            logger.debug( String.format("ErrorReportValve -> response status: %s", response.getStatus()) );
            request.getSession().isNew();

            response.setHeader("Content-Type", "application/json;charset=utf-8");
            response.setHeader("Cache-Control", "no-cache, no-store, mustrevalidate");
            response.setHeader("Pragma", "no-cache");
            //response.sendRedirect("/");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}