package com.skt.help.service.location;

import com.skt.help.repository.NaverRepository;

public class AddressService {
    private final NaverRepository naverRepository;

    public AddressService() {
        this.naverRepository = new NaverRepository();
    }

    public void convert(double latitude, double longitude) {
        naverRepository.convert2Address(latitude, longitude);
    }
}
