package hr.tvz.vibecheck.api.user.mapper;


import hr.tvz.vibecheck.api.user.dto.UserOutputDto;
import hr.tvz.vibecheck.api.account.dto.UpdateAccountRequestDto;
import hr.tvz.vibecheck.api.user.entity.User;
import org.mapstruct.MappingTarget;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper {
    //EDIT
    void updateUserFromRequest(UpdateAccountRequestDto request, @MappingTarget User user);

    //CREATE
    UserOutputDto toUserResponse(User user);
}
