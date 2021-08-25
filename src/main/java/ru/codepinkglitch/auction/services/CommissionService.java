package ru.codepinkglitch.auction.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.codepinkglitch.auction.converters.Converter;
import ru.codepinkglitch.auction.dtos.in.CommissionIn;
import ru.codepinkglitch.auction.entities.CommissionEntity;
import ru.codepinkglitch.auction.repositories.ArtistRepository;
import ru.codepinkglitch.auction.repositories.CommissionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final ArtistRepository artistRepository;
    private final CommissionRepository commissionRepository;
    private final Converter converter;

    public CommissionIn findById(Long id) {
        Optional<CommissionEntity> optional = commissionRepository.findById(id);
        if (optional.isPresent()) {
            return converter.commissionToDto(optional.get());
        } else {
            throw new RuntimeException("No such user.");
        }
    }

    public CommissionIn save(CommissionIn commissionIn) {
        if (commissionIn == null) {
            throw new RuntimeException("Can not save this commission.");
        } else {
            CommissionEntity commission = converter.commissionFromDto(commissionIn);
            return converter.commissionToDto(commissionRepository.save(commission));
        }
    }

    public CommissionIn update(CommissionIn commissionIn) {
        Optional<CommissionEntity> optional = commissionRepository.findById(commissionIn.getId());
        if (optional.isPresent()) {
            CommissionIn commission = converter.commissionToDto(optional.get());
            commission.update(commissionIn);
            return save(commission);
        } else {
            throw new RuntimeException("Can not update this commission.");
        }
    }

    public List<CommissionIn> findByTag(String tag) {
        List<CommissionEntity> list = commissionRepository.findCommissionEntityByTagsContains(tag);
        if(list.isEmpty()) {
            throw new RuntimeException("No such commissions.");
        } else {
            return list.stream()
                    .map(converter::commissionToDto)
                    .collect(Collectors.toList());
        }
    }

    public void remove(Long id) {
        if(commissionRepository.existsById(id)){
            commissionRepository.deleteById(id);
        } else {
            throw new RuntimeException("No such commission.");
        }
    }
}
