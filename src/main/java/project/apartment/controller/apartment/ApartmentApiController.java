package project.apartment.controller.apartment;


import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.apartment.domain.Address;
import project.apartment.domain.Member;
import project.apartment.repository.MemberRepository;
import project.apartment.service.ApartmentService;
import project.apartment.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apartments")
public class ApartmentApiController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ApartmentService apartmentService;

    /*@GetMapping("/near/{email}")
    public ResponseEntity<?> nearbyAptInfo(@PathVariable String email){
        Member member = memberRepository.findByEmail(email);
        Address address = member.getAddress();


    }*/







}
