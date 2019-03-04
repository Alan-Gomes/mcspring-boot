package dev.alangomes.mcspring.warp.service;

import dev.alangomes.mcspring.exception.RemoteException;
import dev.alangomes.mcspring.hook.security.Audict;
import dev.alangomes.mcspring.warp.http.WarpRestClient;
import dev.alangomes.mcspring.warp.model.WarpDTO;
import org.bukkit.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Service
@Profile("web")
class RemoteWarpService implements WarpService {

    @Autowired
    private WarpRestClient warpRestClient;

    @Audict
    @Override
    public WarpDTO create(String name, Location location) {
        WarpDTO warp = new WarpDTO();
        warp.setName(name);
        warp.setLocation(location.clone());
        try {
            Response<WarpDTO> response = warpRestClient.createWarp(warp).execute();
            if (!response.isSuccessful()) {
                throw new RemoteException(response.errorBody().string());
            }
            return response.body();
        } catch (Exception e) {
            throw new RemoteException("Falha ao criar o warp");
        }
    }

    @Override
    public WarpDTO getWarp(String name) {
        try {
            Response<WarpDTO> response = warpRestClient.getWarp(name).execute();
            if (!response.isSuccessful()) {
                throw new RemoteException(response.errorBody().string());
            }
            return response.body();
        } catch (Exception e) {
            throw new RemoteException("Falha ao buscar o warp");
        }
    }

}
