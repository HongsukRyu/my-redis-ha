package com.backend.api.common.utils;

import com.backend.api.common.object.Const;
import com.backend.api.common.object.RequestModel;
import com.backend.api.common.object.Success;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class Utils {

    private static final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };

    /**
     * user host ip get
     *
     * @param request
     * @return ip-v4 string
     */
    public String getClientIpAddress(HttpServletRequest request) {

        for (String header : HEADERS_TO_TRY) {
            if(header == "X-Forwarded-For") {
//                System.out.println("X-Forwarded-For" + " :: [TEST]");
            }
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    public static String CHECK_WHITESPACE_LOWERCASE(String name) {
        return name.replaceAll(" ", "").toLowerCase();
    }

    public static void DELETE_DIR(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                DELETE_DIR(file);
            }
        }

        dir.delete();
    }

    private static final String UNSECURED_CHAR_REGULAR_EXPRESSION =
            "!\"#%&'()*+,.:;<=>?@[]^_`{]+$|.*select.*|.*create.*|.*update.*|.*alter.*|.*delete.*|.*insert.*|.*drop.*|.*--.*|.*union.*|.*join.*";

    private static final Pattern unsecuredCharPattern =
            Pattern.compile(UNSECURED_CHAR_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE);

    public static String makeSecureString(String uri) {
        List<Integer> indexList = findIndexes(uri);
        if (indexList.size() > 3) {
            uri = uri.substring(0, indexList.get(3));
        }
        Matcher matcher = unsecuredCharPattern.matcher(uri);
        return matcher.replaceAll("");
    }

    private static List<Integer> findIndexes(String document) {
        List<Integer> indexList = new ArrayList<>();
        int index = document.indexOf("/");
        while (index != -1) {
            indexList.add(index);
            index = document.indexOf("/", index + "/".length());
        }
        return indexList;
    }

    public static <T> boolean isEmptyOrNull(T collection) {
        if (collection instanceof Map) {
            return ((Map<?, ?>) collection).isEmpty();
        }
        if (collection instanceof List) {
            return ((List<?>) collection).isEmpty();
        }
        if (collection instanceof Set) {
            return ((Set<?>) collection).isEmpty();
        }
        return collection == null;
    }

    public RequestModel getAttributeRequestModel(HttpServletRequest request) {

        RequestModel reqModel = new RequestModel();
        String userId = request.getAttribute("userId").toString();
        int type = Integer.parseInt(request.getAttribute("userType").toString());
        int userGroupId = Integer.parseInt(request.getAttribute("userGroupId").toString());

        reqModel.setUserId(userId);
        reqModel.setType(type);
        reqModel.setUserGroupId(userGroupId);
        return reqModel;
    }

    /**
     * @return String
     */
    public String randNumCreate() {
        StringBuilder tempNum = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int rndVal = (int) (Math.random() * 62);
            if (rndVal < 10) {
                tempNum.append(rndVal);
            } else if (rndVal > 35) {
                tempNum.append((char) (rndVal + 61));
            } else {
                tempNum.append((char) (rndVal + 55));
            }
        }
        return tempNum.toString();
    }

    public LocalDateTime getLocalDateTimeNow() {
        LocalDateTime localDateTime = LocalDateTime.now();
//        ZonedDateTime now = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
        return localDateTime;
    }

    public void sendSlackMessage(String webhookUrl, String message) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpPost httpPost = new HttpPost(env.getProperty("test.slack.webhook"));
            HttpPost httpPost = new HttpPost(webhookUrl);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            String jsonPayload = "{\"text\": \"" + message + "\"}";
            StringEntity entity = new StringEntity(jsonPayload, "UTF-8");
            httpPost.setEntity(entity);
            httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Success> returnEntity(Success success) {
        if (success.isSuccess()) {
            return ResponseEntity.ok().body(success);
        } else if(success.getErrorCode() != null && success.getErrorCode().equals(Const.UNAUTHORIZED)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(success);
        }
        return ResponseEntity.badRequest().body(success);
    }

    public String getStringRoleType(int roleType) {
        String role = "";
        switch (roleType) {
            case Const.USER_TYPE_ADMIN:
                role = Const.ROLE_ADMIN;
                break;
            default:
                role = Const.ROLE_USER;
        }
        return role;
    }

}
