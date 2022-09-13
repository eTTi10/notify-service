package com.lguplus.fleta.provider.external.mmsagent.soap.module.content;

import com.lguplus.fleta.provider.external.mmsagent.soap.module.MM7Context;
import java.io.IOException;
import java.io.OutputStream;

public class UplusContent extends BasicContent {

    private String text;
    private String X_Kmms_SVCCODE;
    private String X_Kmms_redistribution;
    private String X_Kmms_TextInput;
    private String Content_Category;

    public UplusContent() {
    }

    public UplusContent(String text) {
        setText(text);
        setContentType("text/lgtmms-xhtml");
    }

    public String getX_Kmms_SVCCODE() {
        return X_Kmms_SVCCODE;
    }

    public void setX_Kmms_SVCCODE(String x_Kmms_SVCCODE) {
        X_Kmms_SVCCODE = x_Kmms_SVCCODE;
    }

    public String getX_Kmms_redistribution() {
        return X_Kmms_redistribution;
    }

    public void setX_Kmms_redistribution(String x_Kmms_redistribution) {
        X_Kmms_redistribution = x_Kmms_redistribution;
    }

    public String getX_Kmms_TextInput() {
        return X_Kmms_TextInput;
    }

    public void setX_Kmms_TextInput(String x_Kmms_TextInput) {
        X_Kmms_TextInput = x_Kmms_TextInput;
    }

    public String getContent_Category() {
        return Content_Category;
    }

    public void setContent_Category(String content_Category) {
        Content_Category = content_Category;
    }

    @Override
    public void writeTo(OutputStream out, String contentId, MM7Context ctx) throws IOException {
        if (contentId == null) {
            contentId = getContentId();
        }

        StringBuilder b = new StringBuilder();
        b.append("\r\nContent-Type: ").append(getContentType());

        if (getContentLocation() != null && !getContentLocation().equals("")) {
            b.append(";Name=\"").append(getContentLocation()).append("\"");
        }

        if (contentId != null) {
            b.append("\r\nContent-ID: <" + contentId + ">");
        }

        b.append("\r\nContent-Transfer-Encoding: 8bit");

        if (this.getContentLocation() != null) {
            b.append("\r\nContent-Location: ").append(this.getContentLocation());
            b.append("\r\nContent-Disposition: Attachment; Filename=").append(this.getContentLocation());
        }

        if (this.getX_Kmms_SVCCODE() != null) {
            b.append("\r\nX_Kmms_SVCCODE: ").append(this.getX_Kmms_SVCCODE());
        }

        if (this.getX_Kmms_redistribution() != null) {
            b.append("\r\nX_Kmms_redistribution: ").append(this.getX_Kmms_redistribution());
        }

        if (this.getX_Kmms_TextInput() != null) {
            b.append("\r\nX_Kmms_TextInput: ").append(this.getX_Kmms_TextInput());
        }

        b.append("\r\n\r\n");
        out.write(b.toString().getBytes("euc-kr"));
        out.write(text.getBytes("euc-kr"));
        out.flush();
    }

    @Override
    public int getContentLength() {
        return text.length();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version =\"1.0\"  encoding = \"euc-kr\"?>");
        sb.append("<html xmlns=\"http://www.LGTelecom.co.kr/2004/mms/mobiletext/\">");
        sb.append("<head/><body><div>");
        sb.append(text);
        sb.append("</div></body></html>");
        this.text = sb.toString();
    }

}
