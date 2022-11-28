package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.PushTarget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicShowJpaRepository extends JpaRepository<PushTarget, String> {

    void deleteBypKeyAndSaIdAndStbMacAndAlbumIdAndServiceType(Integer pKey, String saId, String stbMac, String albumId, String serviceType);

}
