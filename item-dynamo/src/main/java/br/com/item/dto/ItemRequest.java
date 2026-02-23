package br.com.item.dto;

import br.com.item.validation.Uppercase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ItemRequest {

    @NotBlank(message = "name is required")
    @Size(max = 100)
    @Uppercase // custom validation example
    private String name;

    @Size(max = 500)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

