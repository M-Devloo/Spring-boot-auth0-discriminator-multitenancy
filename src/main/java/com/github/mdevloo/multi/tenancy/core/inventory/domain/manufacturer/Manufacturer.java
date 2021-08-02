package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import com.github.mdevloo.multi.tenancy.fwk.multitenancy.NoMultiTenancy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(schema = "core", name = "manufacturer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@NoMultiTenancy
public class Manufacturer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;
}
