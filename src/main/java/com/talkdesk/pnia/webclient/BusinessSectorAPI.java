package com.talkdesk.pnia.webclient;

import com.talkdesk.pnia.dto.BusinessSectorResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "SectorAPI",
        url = "${app.businessSectorApi.url}"
)
@Headers({
        "Content-Type: application/json"
})
public interface BusinessSectorAPI {

    @GetMapping("/sector/{number}")
    ResponseEntity<BusinessSectorResponse> getSector(@PathVariable("number") final String number);
}
