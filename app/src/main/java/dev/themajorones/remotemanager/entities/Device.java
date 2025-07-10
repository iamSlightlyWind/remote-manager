package dev.themajorones.remotemanager.entities;

import androidx.room.Entity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(tableName = "Device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private List<OS> osList;

    private String host;

    private String username;

    private String password;

    private String keyPath;
}