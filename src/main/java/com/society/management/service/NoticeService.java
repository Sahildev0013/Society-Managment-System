package com.society.management.service;

import com.society.management.dto.NoticeDto;
import com.society.management.model.Notice;
import com.society.management.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedOnDesc();
    }

    public List<Notice> getRecentNotices() {
        return noticeRepository.findTop5ByOrderByCreatedOnDesc();
    }

    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }

    @Transactional
    public Notice createNotice(NoticeDto dto, String postedBy) {
        Notice notice = new Notice(
                dto.getTitle().trim(),
                dto.getMessage().trim(),
                LocalDateTime.now(),
                postedBy
        );
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long id, NoticeDto dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with id: " + id));

        notice.setTitle(dto.getTitle().trim());
        notice.setMessage(dto.getMessage().trim());
        return noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with id: " + id));
        noticeRepository.delete(notice);
    }
}
