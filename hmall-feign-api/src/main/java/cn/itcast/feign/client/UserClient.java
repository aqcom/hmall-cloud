package cn.itcast.feign.client;
import cn.itcast.hmall.pojo.user.Address;
import cn.itcast.hmall.pojo.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@FeignClient("userservice")
public interface UserClient {
    /**
     * 根据地址ID 查询地址信息
     * @param id
     * @return
     */
    @GetMapping("/address/{addressId}")
    public Address getAddressID(@PathVariable("addressId")Long id);

    /**
     * 根据用户id 获取用户信息
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id")Long id);

    /**
     * 扣减用户余额接口
     * @param money
     * @param id
     */
    @RequestMapping("/user/pay/{money}/{id}")
    public void  pay(@PathVariable("money")Long money,@PathVariable("id")Long id);
}
