package dev.themajorones.remotemanager.entities;

import androidx.room.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(tableName = "OS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OS {

    private String name;
    
    private String version;
}