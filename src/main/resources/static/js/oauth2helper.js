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
                if(window.location.pathname == "/")
                {
                    location.replace("/goalssso");
                    console.log("User log in with sso");
                }
                else
                {
                    console.log(window.location.pathname);
                    console.log("It is not an index page");
                }

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