package hr.tvz.vibecheck.dtoMapper;


import hr.tvz.vibecheck.dto.request.EditUserRequest;
import hr.tvz.vibecheck.dto.response.UserEditResponse;
import hr.tvz.vibecheck.dto.response.UserResponse;
import hr.tvz.vibecheck.entity.User;
import org.mapstruct.MappingTarget;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper {
    //EDIT
    UserEditResponse toUserEditResponse(User user);
    void updateUserFromRequest(EditUserRequest request, @MappingTarget User user);

    //CREATE
    UserResponse toUserResponse(User user);

}
