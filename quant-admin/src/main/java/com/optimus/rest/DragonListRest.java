package com.optimus.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "dragon", produces = APPLICATION_JSON)
public class DragonListRest {



}
