package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.UserAddressNotFoundException;
import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.service.UserRoleService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.api.CustomMediaType;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.dto.UserAddressDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path(UriUtils.USERS_URL)
@Component
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRoleService userRoleService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(final UserService userService, final JwtTokenUtil jwtTokenUtil, final UserRoleService userRoleService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRoleService = userRoleService;
    }

    @GET
    @Path("/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userId") final long userId) {
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid @NotNull final RegisterForm registerForm) {
        final User user = userService.createOrConsolidate(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName());
        return Response.created(UriUtils.getUserUri(uriInfo, user.getUserId())).build();
    }

    @PATCH
    @Path("/{userId:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("userId") final long userId,
            @Valid @NotNull final UpdateUserForm updateUserForm
    ) {
        userService.updateUser(
                userId,
                updateUserForm.getNameTrimmedOrNull(),
                updateUserForm.getPreferredLanguage()
        );
        return Response.noContent().build();
    }

    @GET
    @Path("/{userId:\\d+}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddresses(@PathParam("userId") final long userId) {
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        final List<UserAddressDto> dtoList = UserAddressDto.fromUserAddressCollection(uriInfo, user.getAddresses());
        return Response.ok(new GenericEntity<List<UserAddressDto>>(dtoList) {}).build();
    }

    @GET
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddress(
            @PathParam("userId") final long userId,
            @PathParam("addressId") final long addressId
    ) {
        final UserAddress address = userService.getAddressById(userId, addressId).orElseThrow(UserAddressNotFoundException::new);
        final UserAddressDto dto = UserAddressDto.fromUserAddress(uriInfo, address);
        return Response.ok(dto).build();
    }

    @POST
    @Path("/{userId:\\d+}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserAddress(
            @PathParam("userId") final long userId,
            @Valid @NotNull final AddUserAddressForm addUserAddressForm
    ) {
        final UserAddress address = userService.registerAddress(userId, addUserAddressForm.getAddressTrimmedOrNull(), addUserAddressForm.getName());
        return Response.created(UriUtils.getUserAddressUri(uriInfo, address)).build();
    }

    @PATCH
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserAddress(
            @PathParam("userId") final long userId,
            @PathParam("addressId") final long addressId,
            @Valid @NotNull final UpdateUserAddressForm updateUserAddressForm

    ) {
        userService.updateAddress(userId, addressId, updateUserAddressForm.getAddress(), updateUserAddressForm.getName());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserAddress(
            @PathParam("userId") final long userId,
            @PathParam("addressId") final long addressId
    ) {
        userService.deleteAddress(userId, addressId);
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersWithRoleLevel(@Valid @BeanParam final GetUserRoleLevelForm getUserRoleLevelForm) {
        final List<User> users = userRoleService.getByRole(getUserRoleLevelForm.getRoleAsEnum());
        final List<UserDto> userDtos = UserDto.fromUserCollection(uriInfo, users);
        return Response.ok((new GenericEntity<List<UserDto>>(userDtos) {})).build();
    }

    @PATCH
    @Path("/{userId:\\d+}")
    @Consumes(value = {CustomMediaType.USER_ROLE_V1})
    public Response updateUserRoleLevel(
            @PathParam("userId") final long userId,
            @Valid @NotNull final PatchUserRoleLevelForm patchUserRoleLevelForm
    ) {
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        userRoleService.setRole(user.getEmail(), patchUserRoleLevelForm.getRoleAsEnum());
        return Response.ok().build();
    }

    @POST
    @Consumes(value = {CustomMediaType.USER_ROLE_V1})
    public Response createUserRole(@Valid @NotNull final PostUserRoleLevelForm addUserRoleForm) {
        userRoleService.setRole(addUserRoleForm.getEmail(), addUserRoleForm.getRoleAsEnum());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{userId:\\d+}")
    public Response deleteUserRole(@PathParam("userId") final long userId) {
        userRoleService.deleteRole(userId);
        return Response.ok().build();
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_USER_RESETS_PASSWORD)
    public Response createPasswordResetToken(@Valid @NotNull final ResetPasswordForm resetPasswordForm) {
        final User user = userService.getByEmail(resetPasswordForm.getEmail()).orElseThrow(UserNotFoundException::new);
        userService.sendPasswordResetToken(user.getEmail());
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{userId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_USER_RESETS_PASSWORD)
    public Response editUserPasswordWithToken(
            @PathParam("userId") long userId,
            @Valid @NotNull NewPasswordForm newPasswordForm
    ) {
        userService.updatePassword(newPasswordForm.getToken(), newPasswordForm.getPassword());
        return Response.noContent().build();
    }
}
