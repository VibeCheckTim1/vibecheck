package hr.tvz.vibecheck.api.user.mapper;


import hr.tvz.vibecheck.api.user.dto.UserOutputDto;
import hr.tvz.vibecheck.api.account.dto.UpdateAccountRequestDto;
import hr.tvz.vibecheck.api.user.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper {
    //EDIT
    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "avatarPublicId", ignore = true)
    @Mapping(source = "isPrivate", target = "private")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tstamp", ignore = true)
    @Mapping(target = "oauthAccounts", ignore = true)
    @Mapping(target = "playlists", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUserFromRequest(UpdateAccountRequestDto request, @MappingTarget User user);

    //CREATE
    @Mapping(source = "private", target = "isPrivate")
    UserOutputDto toUserResponse(User user);
}
