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

package com.lguplus.fleta.provider.external.mmsagent.soap.module.inf;

import com.lguplus.fleta.provider.external.mmsagent.soap.module.common.StringToEnumMap;
import java.util.Map;
import org.jdom2.Element;

public class Address implements JDOMSupport {

    private RecipientType recipientType = RecipientType.TO;
    private AddressType addressType = AddressType.NUMBER;
    private boolean displayOnly;

    private AddressCoding addressCoding;
    private String id;
    private String address;

    public Address() {
    }

    public Address(String address) {
        setAddress(address);
    }

    public Address(String address, RecipientType recipientType) {
        setAddress(address);
        setRecipientType(recipientType);
    }

    public Address(String address, RecipientType recipientType, AddressType addressType) {
        setAddress(address);
        setRecipientType(recipientType);
        setAddressType(addressType);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AddressCoding getAddressCoding() {
        return addressCoding;
    }

    public void setAddressCoding(AddressCoding addressCoding) {
        this.addressCoding = addressCoding;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public boolean isDisplayOnly() {
        return displayOnly;
    }

    public void setDisplayOnly(boolean displayOnly) {
        this.displayOnly = displayOnly;
    }

    @Override
    public void load(Element e) {
        {
            String value = e.getAttributeValue("displayOnly", e.getNamespace());
            String valueNoNS = e.getAttributeValue("displayOnly");
            if (value != null) {
                displayOnly = Boolean.parseBoolean(value);
            } else if (valueNoNS != null) {
                displayOnly = Boolean.parseBoolean(valueNoNS);
            }
        }
        {
            String value = e.getAttributeValue("addressCoding", e.getNamespace());
            if (value != null) {
                addressCoding = AddressCoding.map.get(value);
            }
        }
        addressType = AddressType.map.get(e.getName());
        id = e.getAttributeValue("id");
        address = e.getText();
    }

    @Override
    public Element save(Element parent) {
        Element e = new Element(addressType.toString(), parent.getNamespace());
        if (displayOnly) {
            e.setAttribute("displayOnly", Boolean.toString(displayOnly), parent.getNamespace());
        }
        if (addressCoding != null) {
            e.setAttribute("addressCoding", addressCoding.toString(), parent.getNamespace());
        }
        if (id != null) {
            e.setAttribute("id", id, parent.getNamespace());
        }
        e.setText(address);
        return e;
    }

    @Override
    public String toString() {
        return address != null ? address : "<unspecified>";
    }

    public enum AddressCoding {
        ENCRYPTED, OBFUSCATED;

        public static final Map<String, AddressCoding> map = new StringToEnumMap<>(AddressCoding.values());

        @Override
        public String toString() {
            return name();
        }
    }

    public enum AddressType {
        RFC822_ADDRESS("RFC822Address"), NUMBER("Number"), SHORT_CODE("ShortCode");

        public static final Map<String, AddressType> map = new StringToEnumMap<AddressType>(AddressType.values());
        private final String display;

        AddressType(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public enum RecipientType {
        TO("To"), CC("Cc"), BCC("Bcc");

        public static final Map<String, RecipientType> map = new StringToEnumMap<RecipientType>(RecipientType.values());
        private final String display;

        RecipientType(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
