/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2014 InstantCom Ltd. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://raw.github.com/vnesek/instantcom-mm7/master/LICENSE.txt
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at appropriate location.
 */

package com.lguplus.fleta.provider.external.mmsagent.soap.module;


import com.lguplus.fleta.provider.external.mmsagent.soap.module.inf.MMSC;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for MMSC implementations. URL connection to connect to MMSC is customizable. Use {@link MM7Context} to configure message serialization/deserialization.
 */
@Slf4j
public abstract class MMSCBase implements MMSC {

    private final Map<String, ?> mmsConfig;
    private String url;
    private MM7Context context;

    public MMSCBase(String url, Map<String, ?> mmsConfig) {
        this.mmsConfig = mmsConfig;
        setUrl(url);
    }

    @Override
    public MM7Response submit(SubmitReq submitReq) throws MM7Error {
        MM7Response rsp = post(submitReq);

        if (!(rsp instanceof SubmitRsp)) {
            // Error
            //throw new MM7Error("failed to instantiate message RSErrorRsp");
            return rsp;
        }
        return rsp;
    }

    private MM7Response post(MM7Request request) throws MM7Error {
        HttpURLConnection conn = null;
        try {
            final MM7Context ctx = getContext();
            final URL u = new URL(getUrl());
            conn = getHttpURLConnection(u);

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setConnectTimeout((int) mmsConfig.get("connecttimeout"));//ex) 3000
            conn.setReadTimeout((int) mmsConfig.get("readtimeout"));//ex) 5000

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", request.getSoapContentType());
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("SOAPAction", "\"\"");

            // HTTP Basic authorization
            if (ctx.getUsername() != null) {
                String authString = ctx.getUsername() + ':' + ctx.getPassword();
                try {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream(128);
                    OutputStream buffer64 = ctx.newBase64OutputStream(buffer);
                    buffer64.write(authString.getBytes("euc-kr"));
                    buffer64.close();
                    conn.setRequestProperty("Authorization", "Basic " + buffer.toString("euc-kr"));
                } catch (IOException ioe) {
                    throw new RuntimeException("Failed to add HTTP Basic Authorization header", ioe);
                }
            }

            //##################################### Log
            //			String logStr = "\n #################  [ MMSCBase ????????????????????? ] ################### \n";
            //			logStr += "\n ping 61.101.246.151:8887 \n";
            //			logStr += "\n ping 222.231.12.68:8887 \n";
            //			logStr += "\n ?????? ????????? ???????????? ???????????? ???????????? ?????? ????????? ip??? ???????????? ????????? \n";
            //			logStr += "\n ????????? set?????? ?????? get???????????? ?????? \n";
            //			log.info(logStr);
            //#####################################

            final OutputStream out = conn.getOutputStream();
            try {
                MM7Message.save(request, out, ctx);
            } finally {
                out.flush();
                out.close();
            }

            String contentType = conn.getContentType();
            if (contentType != null && (contentType.startsWith("text/xml") || contentType.startsWith("multipart/"))) {
                InputStream in;
                try {
                    in = conn.getInputStream();
                } catch (IOException e) {
                    if (conn.getResponseCode() >= 400) {
                        in = conn.getErrorStream();
                    } else {
                        throw e;
                    }
                }
                try {
                    return (MM7Response) MM7Message.load(in, contentType, getContext());
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } else {
                throw new MM7Error("unexpected content type: " + contentType + ", status: " + //
                    conn.getResponseCode() + " " + conn.getResponseMessage() + ", content: " + //
                    conn.getContent());
            }
        } catch (IOException ioe) {
            throw new MM7Error("IO error: " + ioe.getMessage(), ioe);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    protected abstract HttpURLConnection getHttpURLConnection(URL u) throws IOException;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public MM7Context getContext() {
        if (context == null) {
            context = new MM7Context();
        }
        return context;
    }

    public void setContext(MM7Context context) {
        this.context = context;
    }
}
