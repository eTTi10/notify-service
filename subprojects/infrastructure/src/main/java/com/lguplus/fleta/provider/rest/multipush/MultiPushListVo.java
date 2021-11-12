package com.lguplus.fleta.provider.rest.multipush;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MultiPushListVo {
	private String msg;
	private MultiPushItem items;
	private MultiPushUser users;

	@Getter
	@Setter
	@NoArgsConstructor
	@ToString
	public static class MultiPushItem {
		private List<String> item;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class MultiPushUser {
		private List<String> reg_id;
		private String p_reg_id = "";
		private String transaction_id = "";

		public MultiPushUser(String p_reg_id, String transaction_id) {
			this.p_reg_id = p_reg_id;
			this.transaction_id = transaction_id;
		}
	}
}
