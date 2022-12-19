package exam.model.entities;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigInteger;

@Entity
@Table(name = "shops")
public class Shop extends BaseEntity {

    @Column(unique = true)
    private String name;

    @Column
    private BigInteger income;

    @Column
    private String address;

    @Column(name = "employee_count")
    private Integer employeeCount;

    @Column(name = "shop_area")
    private Integer shopArea;

    @OneToOne
    private Town town;

    public Shop() {
    }

    public Shop(String name, BigInteger income, String address, Integer employeeCount, Integer shopArea) {
        this.name = name;
        this.income = income;
        this.address = address;
        this.employeeCount = employeeCount;
        this.shopArea = shopArea;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getIncome() {
        return income;
    }

    public void setIncome(BigInteger income) {
        this.income = income;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Integer getShopArea() {
        return shopArea;
    }

    public void setShopArea(Integer shopArea) {
        this.shopArea = shopArea;
    }
}
