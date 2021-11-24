package com.lguplus.fleta.provider.rest.announce;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

@FeignClient(name = "announce-client", url="localhost:8010")
public interface FeignAnnounceInterface {
    @PostMapping(value = "/example/announce", produces = "application/json")
    public AnnounceResponse sendAnnounceMessage1(@RequestBody AnnounceRequest human);

    @PostMapping(value = "/example/announce", produces = {"application/json; charset=euc-kr"})
    public AnnounceResponse sendAnnounceMessage2(@RequestBody HashMap<String,Object> map);

    @PostMapping(value = "${service.channel.url}/announce", produces = {"application/json; charset=euc-kr"})
    public AnnounceResponse sendAnnounceMessage3(@RequestBody HashMap<String,Object> map);
}
