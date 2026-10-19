package com.lucas.nestor_ai.infra.http.controller.ai;

import com.lucas.nestor_ai.application.service.ai.TranscribeAudioUseCase;
import com.lucas.nestor_ai.infra.http.helper.AudioValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class TransactionAiController {

    private final TranscribeAudioUseCase transcribeAudioUseCase;
    private final AudioValidatorHelper audioValidatorHelper;

    @PostMapping(
            value = "/transcriptions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "audio/mpeg"
    )
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {

        audioValidatorHelper.validate(file);

        byte[] audio = transcribeAudioUseCase.execute(file.getResource());

        ByteArrayResource resource = new ByteArrayResource(audio);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("response.mp3")
                                .build()
                                .toString()
                )
                .contentLength(audio.length)
                .body(resource);
    }
}
