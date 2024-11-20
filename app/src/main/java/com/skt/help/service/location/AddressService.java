package com.skt.help.service.location;

import com.skt.help.repository.NaverRepository;
import com.skt.help.repository.NaverRepository.ReverseGeocodeCallback;

public class AddressService {
    private final NaverRepository naverRepository;

    public AddressService() {
        this.naverRepository = new NaverRepository();
    }

    public void convert(String coordinate, ReverseGeocodeCallback callback) {
        naverRepository.convert2Address(coordinate, callback);
    }
}
