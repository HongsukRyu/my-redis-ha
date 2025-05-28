package com.backend.api.common.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class ClickLogFilter extends AbstractMatcherFilter<ILoggingEvent> {
    String word;
    public void setString(String word) {
        this.word = word;
    }
    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent.getMessage().contains(word)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }
}