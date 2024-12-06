package com.iscp.backend.components;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.iscp.backend.models.Enum;

import java.io.IOException;

public class DepartmentTypeSerializer extends JsonSerializer<Enum.DepartmentType> {
    @Override
    public void serialize(Enum.DepartmentType departmentType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(departmentType.getDisplayName());
    }
}
