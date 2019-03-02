function isLoginWithSso()
{
    var url = "/isloginwithsso";

    $.ajax(
    {
        url: url,
        type: "GET",
        success: function(res)
        {
            if(res)
            {
                console.log(res);
            }
            else
            {
                console.log("User does not log in with sso");
                console.log(err);
            }
        },
        error: function(err)
        {
            console.log("User does not log in with sso");
            console.log(err);
        }
    })
}

$(document).ready(function()
{
    isLoginWithSso();
});