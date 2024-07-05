package is2.g57.hopetrade.runner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import is2.g57.hopetrade.dto.OfertaDTO;
import is2.g57.hopetrade.entity.Oferta;
import is2.g57.hopetrade.mapper.OfertaMapper;
import is2.g57.hopetrade.repository.OfertaRepository;
import is2.g57.hopetrade.services.ImageService;

@Component
@Order(9)
public class OfertaDataLoader implements ApplicationRunner {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private OfertaMapper ofertaMapper;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<String[]> ofertas = Arrays.asList(
            new String[]{"4", "Manzana", "Una manzana fresca y jugosa, llena de vitaminas y perfecta como merienda saludable.", "1", "1", "ACEPTADA", "manzana.jpg"},
            new String[]{"4", "Lavarropas", "Un lavarropas eficiente que facilita la limpieza de tu ropa con tecnología moderna.", "6", "2", "ACEPTADA", "lavarropas.jpg"},
            new String[]{"4", "Fideos", "Paquete de fideos tallarines", "1", "1", "ACEPTADA", "tallirin.jpg"}
        );

        if (ofertaRepository.count() == 0) {
            System.out.println("Cargando ofertas de ejemplo...");
            for (String[] o: ofertas) {
                OfertaDTO dto = new OfertaDTO();
                dto.setUserId(Long.parseLong(o[0]));
                dto.setTitulo(o[1]);
                dto.setDescripcion(o[2]);
                dto.setPublicacionId(Long.parseLong(o[3]));  // Assuming Publicacion ID
                dto.setFilialId(Long.parseLong(o[4]));  // Assuming Filial ID
                dto.setEstado(o[5]);
                dto.setImagen(imageService.loadSampleBase64(o[6]));
                dto.setFechaCreacion(LocalDateTime.now());
                dto.setFechaIntercambio(LocalDateTime.now().plusDays(30));  // Set a default exchange date
                Oferta oferta = ofertaMapper.map(dto);
                ofertaRepository.save(oferta);
            }
        }
    }
}
