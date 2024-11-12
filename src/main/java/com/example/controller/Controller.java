package com.example.controller;

import com.example.MessageDto;
import com.example.annotation.RequestBody;

public interface Controller {

    MessageDto parseStringToJson(@RequestBody MessageDto dto);
}
