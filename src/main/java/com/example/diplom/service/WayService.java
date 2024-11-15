package com.example.diplom.service;

import com.example.diplom.exceptions.CustomException;
import com.example.diplom.model.db.entity.DescriptionWay;
import com.example.diplom.model.db.entity.Way;
import com.example.diplom.model.db.repository.WayDescriptionRepository;
import com.example.diplom.model.db.repository.WayRepository;
import com.example.diplom.model.dto.request.WayDescriptionInfoRequest;
import com.example.diplom.model.dto.request.WayInfoRequest;
import com.example.diplom.model.dto.response.WayDescriptionInfoResponse;
import com.example.diplom.model.dto.response.WayInfoResponse;
import com.example.diplom.model.enums.WayStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class WayService {

    private final WayRepository wayRepository;
    private final WayDescriptionRepository wayDescriptionRepository;
    private final ObjectMapper mapper;

    public WayInfoResponse addWay(WayInfoRequest request) {

        Way way = mapper.convertValue(request, Way.class);

        way.setDateAdded(LocalDateTime.now());
        way.setStatus(WayStatus.ADDED);

        Way saved = wayRepository.save(way);

        return mapper.convertValue(saved, WayInfoResponse.class);
    }

    public Way getWayFromDB(Long id) {
        Way way;
        Optional<Way> someWay = wayRepository.findById(id);
        if( someWay.isPresent() ) {
            way = someWay.get();
        } else {
            way = null;
        }
        return way;
    }

    public boolean checkWay(Long id) {
        Way way = getWayFromDB(id);
        if( way != null ) {
            if( way.getStatus() != WayStatus.DELETED ) {
                return true;
            }
        }
        return false;
    }

    public WayInfoResponse getWay(Long id) {
        Way way = getWayFromDB(id);

        List<WayDescriptionInfoResponse> answerPartsWay = List.of(new WayDescriptionInfoResponse());
        if( way != null ) {
            answerPartsWay = way.getDescriptionWays().stream()
                    .map(descriptionWay -> mapper.convertValue(descriptionWay, WayDescriptionInfoResponse.class))
                    .collect(Collectors.toList());
        } else {
            // prepare for exception
            throw new CustomException(String.format("Маршрута с id [%d] нет.", id), HttpStatus.BAD_REQUEST);
            //way = new Way();
        }
        WayInfoResponse answer = mapper.convertValue(way, WayInfoResponse.class);
        answer.setWayDescriptionInfoResponseList(answerPartsWay);
        return answer;
    }

    public WayInfoResponse updateWay(Long id, WayInfoRequest request) {
        Way way = getWayFromDB(id);
        Way saved = new Way();
        if( way != null ) {
            if( way.getStatus() != WayStatus.DELETED ) {
                way.setDescription(request.getDescription() == null ? way.getDescription() : request.getDescription());
                way.setWayNumber(request.getWayNumber() == null ? way.getWayNumber() : request.getWayNumber());
                way.setCost(request.getCost() == null ? way.getCost() : request.getCost());
                way.setWayLength(request.getWayLength() == null ? way.getWayLength() : request.getWayLength());
                way.setDateModified(LocalDateTime.now());
                way.setStatus(WayStatus.UPDATED);

                saved = wayRepository.save(way);
            } else {
                // prepare for exception
                // изменить данные удаленного маршрута нельзя
                throw new CustomException(String.format("Изменить данные удаленного маршрута нельзя."), HttpStatus.BAD_REQUEST);
            }
        } else {
            // prepare for exception
            throw new CustomException(String.format("Маршрута с id [%d] нет.", id), HttpStatus.BAD_REQUEST);
            //way = new Way();
        }

        return mapper.convertValue(saved, WayInfoResponse.class);
    }

    public void deleteWay(Long id) {
        Way way = getWayFromDB(id);
        if( way != null ) {
            if( way.getStatus() != WayStatus.DELETED ) {
                way.setDateDeleted(LocalDateTime.now());
                way.setStatus(WayStatus.DELETED);

                wayRepository.save(way);
            } else {
                // prepare for exception
                // изменить данные удаленного маршрута нельзя
                throw new CustomException(String.format("Удалить удаленный маршрут нельзя."), HttpStatus.BAD_REQUEST);
            }
        } else {
            // prepare for exception
            // маршрута то и нет
            throw new CustomException(String.format("Маршрута с id [%d] нет.", id), HttpStatus.BAD_REQUEST);
        }
    }

    public List<WayInfoResponse> getAllWays() {
        //return Collections.singletonList(new WayInfoResponse());
        return wayRepository.findAll().stream()
                .map(way -> mapper.convertValue(way, WayInfoResponse.class))
                .collect(Collectors.toList());
    }


    // Подробное описание маршрута
    // ---------------------------
    public WayDescriptionInfoResponse addPartWay(Long id, WayDescriptionInfoRequest request) {
        Way way = getWayFromDB(id);
        Short partNumber;

        if( way != null ) {
            partNumber = request.getPartNumber();
            Optional<DescriptionWay> somePartWay = wayDescriptionRepository.findByWayIdPartNumber(id, partNumber);
            if( somePartWay.isPresent() ) {
                // Prepare for exception
                // Такая часть маршрута уже есть
                throw new CustomException(String.format("Часть маршрута [%d] уже внесена.", partNumber), HttpStatus.BAD_REQUEST);
            } else {
                DescriptionWay saved;
                DescriptionWay descriptionWay = mapper.convertValue(request,DescriptionWay.class);
                descriptionWay.setWay(way);
                descriptionWay.setDateAdded(LocalDateTime.now());
                descriptionWay.setStatus(WayStatus.ADDED);

                saved = wayDescriptionRepository.save(descriptionWay);
                return mapper.convertValue(saved, WayDescriptionInfoResponse.class);
            }
        } else {
            // prepare for exception
            // Нельзя добавить часть маршрута для несуществующего маршрута
            throw new CustomException(String.format("Нельзя добавить часть маршрута для несуществующего маршрута."), HttpStatus.BAD_REQUEST);
        }
        // After added exception return statement doesn't needed
        //return null;
    }

    public List<WayDescriptionInfoResponse> getPartsWay(Long id) {
        List<WayDescriptionInfoResponse> answer = List.of(new WayDescriptionInfoResponse());
        Way way = getWayFromDB(id);

        if( way != null ) {
            answer = way.getDescriptionWays().stream()
                    .map(descriptionWay -> mapper.convertValue(descriptionWay, WayDescriptionInfoResponse.class))
                    .collect(Collectors.toList());
        } else {
            // prepare for exception
            throw new CustomException(String.format("Маршрута с id [%d] нет.", id), HttpStatus.BAD_REQUEST);
        }
        return answer;
    }

    public WayDescriptionInfoResponse updatePartWay(Long id, Short partNumber, WayDescriptionInfoRequest request) {
        DescriptionWay partWay;
        DescriptionWay saved;
        Optional<DescriptionWay> somePartWay = wayDescriptionRepository.findByWayIdPartNumber(id, partNumber);
        if( somePartWay.isPresent() ) {
            partWay = somePartWay.get();
        } else {
            partWay = null;
        }

        if( partWay != null ) {
            // !!! Attention: partNumber impossible to change with this method
            partWay.setNameStreet(request.getNameStreet() == null ? partWay.getNameStreet() : request.getNameStreet());
            partWay.setPartStart(request.getPartStart() == null ? partWay.getPartStart() : request.getPartStart());
            partWay.setPartEnd(request.getPartEnd() == null ? partWay.getPartEnd() : request.getPartEnd());
            partWay.setManeuver2nextPart(request.getManeuver2nextPart() == null ? partWay.getManeuver2nextPart() : request.getManeuver2nextPart());
            partWay.setPartLength(request.getPartLength() == null ? partWay.getPartLength() : request.getPartLength());
            partWay.setDateModified(LocalDateTime.now());
            partWay.setStatus(WayStatus.UPDATED);

            saved = wayDescriptionRepository.save(partWay);

        } else {
            // prepare for exception
            throw new CustomException(String.format("[%d] такая часть маршрута не описана.", partNumber), HttpStatus.BAD_REQUEST);
            //saved = new DescriptionWay();
        }

        return mapper.convertValue(saved, WayDescriptionInfoResponse.class);
    }

    public void deletePartWay(Long id, Short partNumber) {
        Optional<DescriptionWay> somePartWay = wayDescriptionRepository.findByWayIdPartNumber(id, partNumber);
        if( somePartWay.isPresent() ) {
            wayDescriptionRepository.delete(somePartWay.get());
        }
    }
}
