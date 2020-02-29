package com.jiankang.splitfile.service;

import com.jiankang.splitfile.bean.Model;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


@Service
public interface CalculateBillService {


    public LinkedHashMap<String, Model> calculateBill(File excelFiles);
}
