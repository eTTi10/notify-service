package com.lguplus.fleta.data.entity;

import com.lguplus.fleta.data.entity.id.DeviceInfoId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@Entity
@Table(name = "SMARTUX.PT_PUSH_TARGET_INFO")
@IdClass(DeviceInfoId.class)
public class DeviceInfoEntity implements Serializable {
    @Id
    @Column(name = "sa_id")
    private String saId;

    @Id
    @Column(name = "service_type")
    private String serviceType;

    @Id
    @Column(name = "agent_type")
    private String agentType;

    @Column(name = "noti_type")
    private String notiType;
}
