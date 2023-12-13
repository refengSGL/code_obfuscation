package com.refengSGL.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.refengSGL.entity.Directory;
import com.refengSGL.mapper.DirectoryMapper;
import com.refengSGL.service.DirectoryService;
import org.springframework.stereotype.Service;

@Service
public class DirectoryServiceImpl extends ServiceImpl<DirectoryMapper, Directory> implements DirectoryService {
}
