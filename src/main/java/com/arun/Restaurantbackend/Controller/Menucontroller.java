package com.arun.Restaurantbackend.Controller;


import com.arun.Restaurantbackend.DTO.ItemDto;
import com.arun.Restaurantbackend.DTO.MenuDto;
import com.arun.Restaurantbackend.Entity.Item;
import com.arun.Restaurantbackend.Exception.FileNotUploadException;
import com.arun.Restaurantbackend.Service.CloudinaryService;
import com.arun.Restaurantbackend.Service.MenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/menu")
@Tag(name = "9. Menu Apis", description = "Give Access to add/remove/update menu  of  restaurant ")
public class Menucontroller {

private final ModelMapper modelMapper;
    private final MenuService menuService;
private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/additem/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuDto> addmenuitem(@RequestPart("data")  @Valid Item menuItem,
                                               @RequestPart("file")MultipartFile multipartFile,
                                               @PathVariable Long id) {
        String s="";
        try {
          s = cloudinaryService.addImage(multipartFile,"Item");
        }
        catch (Exception e){
            log.info(e.getMessage()+"....................................................................................................");
            throw new FileNotUploadException("file is not Uploaded ");

        }
        menuItem.setImageUrl(s);


        MenuDto menuDto = menuService.addmenuitem(menuItem, id);
        return ResponseEntity.ok(menuDto);
    }
   @CacheEvict(cacheNames = "orderitem",key = "#result.body.id")
    @DeleteMapping("/removeitem/{id}")
    public ResponseEntity<MenuDto> removemenuitem(@RequestBody  @Valid  Item menuItem, @PathVariable Long id) {
        MenuDto menuDto = menuService.removemenuitem(menuItem, id);
        return ResponseEntity.ok(menuDto);
    }

    @CachePut(cacheNames = "orderitem",key = "#result.body.id")
    @PatchMapping("/updateprice/{id}/{price}")
    public ResponseEntity<ItemDto> updatemenuitem(@PathVariable  Long id, Long price) {
        ItemDto item = menuService.updateprice(id, price);
        return ResponseEntity.ok(item);
    }




}
