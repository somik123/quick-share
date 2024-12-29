package org.somik.quick_share.controller;

import java.util.List;

import org.somik.quick_share.dto.MessageBoxDTO;
import org.somik.quick_share.dto.MessageDTO;
import org.somik.quick_share.dto.RequestDTO;
import org.somik.quick_share.dto.ResponseDTO;
import org.somik.quick_share.service.MessageBoxService;
import org.somik.quick_share.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    @Autowired
    MessageBoxService messageBoxService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("fileshare_site_url", System.getenv("FILESHARE_SITE_FULL_URL"));
        return "home";
    }

    @PostMapping("/createMessageBox")
    public String createMessageBox(@RequestBody RequestDTO requestDTO, Model model, HttpServletRequest request) {
        String creatorIp = CommonUtils.getClientIpAddress(request);
        ResponseDTO response = messageBoxService.createMessageBox(requestDTO.getMsgBoxName().toLowerCase(),
                requestDTO.getMsgBoxPass(), creatorIp);
        if (response.getStatus() == "OK") {
            model.addAttribute("success", "Message box created successfully.");
        } else {
            model.addAttribute("error", response.getError());
        }
        return "messages";
    }

    @PostMapping("/openMessageBox")
    public String openMessageBox(@RequestBody RequestDTO requestDTO, Model model) {
        ResponseDTO response = messageBoxService.openMessageBox(requestDTO.getMsgBoxName().toLowerCase(),
                requestDTO.getMsgBoxPass());
        model = addMessageToModel(model, response);
        return "messages";
    }

    @PostMapping("/postMessage")
    public String postMessage(@RequestBody RequestDTO requestDTO, Model model, HttpServletRequest request) {
        String creatorIp = CommonUtils.getClientIpAddress(request);
        ResponseDTO response = messageBoxService.addMessageToBox(requestDTO.getMsgBoxName().toLowerCase(),
                requestDTO.getMsgBoxPass(),
                requestDTO.getUsername(), requestDTO.getMessage(), requestDTO.getExpiry(), creatorIp);
        model = addMessageToModel(model, response);
        return "messages";
    }

    @PostMapping("/deleteMessage")
    public String deleteMessage(@RequestBody RequestDTO requestDTO, Model model) {
        ResponseDTO response = messageBoxService.deleteMessageFromBox(requestDTO.getMsgBoxName().toLowerCase(),
                requestDTO.getMsgBoxPass(), requestDTO.getMessageDeleteCode());
        model = addMessageToModel(model, response);
        return "messages";
    }

    private Model addMessageToModel(Model model, ResponseDTO response) {
        MessageBoxDTO messageBoxDTO = (MessageBoxDTO) response.getContent();
        if (messageBoxDTO != null) {
            List<MessageDTO> messageList = messageBoxDTO.getMessageList();
            if (messageList != null && messageList.size() > 0) {
                model.addAttribute("max", messageList.size() - 1);
                model.addAttribute("messageList", messageList);
            } else {
                model.addAttribute("success", "Message box opened, no messages.");
            }
        }

        if (response.getStatus() != "OK") {
            model.addAttribute("error", response.getError());
        }
        return model;
    }

    @GetMapping("/{messageBoxName}")
    public String homePageAlt(Model model, @PathVariable String messageBoxName) {
        model.addAttribute("messageBoxName", messageBoxName);
        return homePage(model);
    }
}
