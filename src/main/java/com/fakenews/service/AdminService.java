package com.fakenews.service;

import java.util.*;

import com.fakenews.model.Domain;
import com.fakenews.model.Report;
import com.fakenews.model.User;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    public List<Report> reports = new ArrayList<>();
    public List<User> users = new ArrayList<>();
    public List<Domain> domains = new ArrayList<>();


}
