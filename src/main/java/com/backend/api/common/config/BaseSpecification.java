package com.backend.api.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.api.common.object.SearchPropObject;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings("rawtypes")
public class BaseSpecification {
    public static Specification<Object> makeSpec(String searchOptions, Map<String, String> condition) {
        return((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            Map<String, String> filter = new HashMap<>();
            String[] prop = searchOptions.split("\\|\\|");

            if(searchOptions.isEmpty() && (condition == null || condition.isEmpty()))
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            for(String option : prop) {
                convertDataToPredicate(criteriaBuilder, predicates, root, option);
            }

            if(condition != null) {
                for (Map.Entry<String, String> stringStringEntry : condition.entrySet()) {
                    @SuppressWarnings("rawtypes") Map.Entry entry = stringStringEntry;
                    convertDataToPredicate(criteriaBuilder, predicates, root, (String) entry.getValue());
                }
//                condition.forEach((key, value)->{
//                    convertDataToPredicate(criteriaBuilder, predicates, root, value);
//                });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private static void convertDataToPredicate(CriteriaBuilder criteriaBuilder,
                                               List<Predicate> predicates,
                                               jakarta.persistence.criteria.Root<Object> root,
                                               String option) {
        SearchPropObject propObj = makeProperty(option);
        if(propObj == null || propObj.getValue() == null || propObj.getValue().isEmpty())
            return;

        switch (propObj.getType().toLowerCase(Locale.ROOT)) {
            case "number":
                if(propObj.getSign().equals("between")) {
                    String[] val = propObj.getValue().split(",");
                    if(val.length==1)
                        return;
                    predicates.add(makePredicate(criteriaBuilder, root, propObj.getColumn(),
                            val[0], val[1], propObj.getSign(), propObj, Integer.class));
                } else {
                    predicates.add(makePredicate(criteriaBuilder, root, propObj.getColumn(),
                            propObj.getValue(), null, propObj.getSign(), propObj, Integer.class));
                }
                break;
            case "string":
                predicates.add(makePredicate(criteriaBuilder, root, propObj.getColumn(),
                        propObj.getValue(), null, propObj.getSign(), propObj, String.class));
                break;
            case "datetime":
                if(propObj.getSign().equals("between")) {
                    String[] val = propObj.getValue().split(",");
                    if(val.length==1)
                        return;

                    if(val[0].trim().length() < 11)
                        val[0] += " 00:00:00";

                    if(val[1].trim().length() < 11)
                        val[1] += " 23:59:59";

                    LocalDateTime date1 = LocalDateTime.parse(val[0].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime date2 = LocalDateTime.parse(val[1].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    predicates.add(makePredicate(criteriaBuilder, root, propObj.getColumn(),
                            date1, date2, propObj.getSign(), propObj, LocalDateTime.class));
                } else if(propObj.getSign().equals("eq")) {
                    LocalDateTime date1 = LocalDateTime.parse(propObj.getValue().trim() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime date2 = LocalDateTime.parse(propObj.getValue().trim() + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    predicates.add(makePredicate(criteriaBuilder, root, propObj.getColumn(),
                            date1, date2, "between", propObj, LocalDateTime.class));
                } else {
                    LocalDateTime date1 = LocalDateTime.parse(propObj.getValue() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    predicates.add(makePredicate(criteriaBuilder, root, propObj.getColumn(),
                            date1, null, propObj.getSign(), propObj, LocalDateTime.class));
                }
                break;
            case "boolean":
                if (propObj.getValue().equals("0") || propObj.getValue().toLowerCase(Locale.ROOT).equals("false")) {
                    propObj.setValue("false");
                } else if (propObj.getValue().equals("1") || propObj.getValue().toLowerCase(Locale.ROOT).equals("true")) {
                    propObj.setValue("true");
                } else {
                    propObj.setValue("false");
                }

                predicates.add(makePredicate(criteriaBuilder, root, propObj.getColumn(),
                        propObj.getValue(), null, propObj.getSign(), propObj, Boolean.class));
                break;
        }
    }

    private static Predicate makePredicate(CriteriaBuilder criteriaBuilder,
                                           jakarta.persistence.criteria.Root<Object> root,
                                           String key, Object value, Object value2, String sign, SearchPropObject propObj, Class cls) {

        ObjectMapper om = new ObjectMapper();
        switch (sign) {
            case "like":
                return criteriaBuilder.like(
                        root.get(key).as(cls), "%" + value + "%");
            case "eq":
                return criteriaBuilder.equal(
                        root.get(key).as(cls), om.convertValue(value, cls));
            case "ne":
                return criteriaBuilder.notEqual(
                        root.get(key).as(cls), om.convertValue(value, cls));
            case "lt":
                return criteriaBuilder.lt(
                        root.get(key).as(cls), Integer.valueOf(value.toString()));
            case "le":
                return criteriaBuilder.le(
                        root.get(key).as(cls), Integer.valueOf(value.toString()));
            case "gt":
                return criteriaBuilder.gt(
                        root.get(key).as(cls), Integer.valueOf(value.toString()));
            case "ge":
                return criteriaBuilder.ge(
                        root.get(key).as(cls), Integer.valueOf(value.toString()));
            case "in":
                String[] inValues = propObj.getInValues().split("\\|\\|");
                List<String> inList = new ArrayList<>();
                inList.add(value.toString());
                if(inValues.length == 0)
                    inList.add("");
                else {
                    inList.addAll(Arrays.asList(inValues));
                }

                return criteriaBuilder.in(root.get(key)).value(inList);

            case "between":
                if(cls.equals(Integer.class))
                    return criteriaBuilder.between(root.get(key).as(cls), Integer.valueOf(value.toString()), Integer.valueOf(value2.toString()));
                else
                    return criteriaBuilder.between(root.get(key).as(cls), (LocalDateTime)value, (LocalDateTime)value2);
        }

        return null;
    }

    private static SearchPropObject makeProperty(String query) {
        SearchPropObject propObj = null;

        String[] cols = query.split("::");

        if(cols.length > 4) {
            // IN type 처리시 inValues 포함
            propObj = new SearchPropObject(cols[0], cols[1], cols[2], cols[3], cols[4]);
        } else if(cols.length > 3) {
            // 기본형 type 처리시 inValues 제외
            propObj = new SearchPropObject(cols[0], cols[1], cols[2], cols[3]);
        }

        return propObj;
    }
}
