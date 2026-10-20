package com.cfs.BookMyShow.entity;

import jakarta.persistence.*;
import lombok.*;
//bhot sare theater means inox  hai ek but in many places one inox present in many states and  but that is one inox only
@Entity
@Table(name = "theaters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) // we are not stateing unique =truew becase therer will be many theater with the same name   we idnetitfy with they address
    private String name;

    private String address;
 
    @ManyToOne
    @JoinColumn(name="city_id",nullable = false)// as i said earlier here the city_id in join column is the foriegn key in db table and we not create any table and db then we have to write thing in format that mysql take or orm use it so in db in table  the foreign key starts with table name in small casae and then underscore(_)  and id and the table name means the foreign key table's id
    private City city;
}
//and note in foreign key ,the key the written in join column that not plural that is singular with underscore id like :- city_id
// where the joincolumn we put on which side// see when we set the mapping after atht we have to decide where the join column come  that totally depend on you from where you see what like if see from the theator  to city then on theater join column come if see from   the city to theator then join column come on city
