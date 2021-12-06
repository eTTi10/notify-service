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

package com.lguplus.fleta.provider.soap.mmsagent.module;

import com.lguplus.fleta.provider.soap.mmsagent.module.inf.Address;
import org.jdom2.Element;

public class MM7Request extends MM7Message {

	public String getVasId() {
		return vasId;
	}

	public String getVaspId() {
		return vaspId;
	}

	public void setVasId(String vasId) {
		this.vasId = vasId;
	}

	public void setVaspId(String vaspId) {
		this.vaspId = vaspId;
	}

	public Element save(Element parent) {
		Element e = super.save(parent);
		if (vaspId != null || vasId != null) {
			Element si = new Element("SenderIdentification");
			if (relayServerId != null) {
				si.addContent(new Element("MMSRelayServerID").setText(relayServerId));
			}
			if (vaspId != null) {
				si.addContent(new Element("VASPID").setText(vaspId));
			}
			if (vasId != null) {
				si.addContent(new Element("VASID").setText(vasId));
			}
			if (senderAddress != null) {
				Element sa = new Element("SenderAddress");
				si.addContent(sa);
				if (senderAddress.getAddressType() != null) {
					sa.addContent(senderAddress.save(sa));
				} else {
					sa.addContent(senderAddress.getAddress());
				}
			}
			if (callbackAddress != null) {
				Element sa = new Element("CallBack");
				si.addContent(sa);
				if (callbackAddress.getAddressType() != null) {
					sa.addContent(callbackAddress.save(sa));
				} else {
					sa.addContent(callbackAddress.getAddress());
				}
			}
			e.addContent(si);
		}
		return e;
	}

	public MM7Response reply() {
		throw new UnsupportedOperationException("should be overriden by subclass");
	}

	@Override
	public void load(Element element) {
		super.load(element);

		Element body = element.getChild("Body", MM7Message.ENVELOPE);
		Element req = (Element) body.getChildren().get(0);
		setVasId(req.getChildTextTrim("VASID", req.getNamespace()));
		setVaspId(req.getChildTextTrim("VASPID", req.getNamespace()));
		setRelayServerId(req.getChildTextTrim("MMSRelayServerID", req.getNamespace()));
	}

	public void setRelayServerId(String relayServerId) {
		this.relayServerId = relayServerId;
	}

	public String getRelayServerId() {
		return relayServerId;
	}

	public void setSenderAddress(Address senderAddress) {
		this.senderAddress = senderAddress;
	}

	public Address getSenderAddress() {
		return senderAddress;
	}
	
	public void setCallBackAddress(Address callbackAddress) {
		this.callbackAddress = callbackAddress;
	}

	public Address getCallBackAddress() {
		return callbackAddress;
	}

	private Address senderAddress;
	private Address callbackAddress;
	private String relayServerId;
	private String vaspId;
	private String vasId;
}
