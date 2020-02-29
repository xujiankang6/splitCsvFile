package com.jiankang.splitfile.service;

import com.jiankang.splitfile.bean.Model;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;


@Service
public interface CalculateBillService {


    public Map<String, Model> calculateBill(File excelFiles);
}
