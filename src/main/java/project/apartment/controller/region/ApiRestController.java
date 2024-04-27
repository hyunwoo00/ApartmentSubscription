package project.apartment.controller.region;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.apartment.controller.region.dto.RegionInfo;
import project.apartment.domain.Region;
import project.apartment.repository.RegionRepository;
import project.apartment.service.RegionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiRestController {

    private final RegionRepository regionRepository;
    private final RegionService regionService;

    @GetMapping("/region")
    public ResponseEntity<?> getRegionController() throws Exception {
        return ResponseEntity.ok(regionService.storeRegions());
    }

    @GetMapping("/api/region")
    public List<RegionInfo> getRegion() throws Exception{
        List<Region> region = regionRepository.getRegion();
        return region.stream()
                .map(a -> new RegionInfo(a.getSido(), a.getSgg()))
                .toList();

    }




}
