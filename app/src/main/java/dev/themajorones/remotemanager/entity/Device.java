package dev.themajorones.remotemanager.entity;

import androidx.room.Entity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity(tableName = "Device")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private List<OS> osList;

    private String host;

    private String username;

    private String password;

    private String keyPath;
}