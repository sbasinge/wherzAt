package services

import play.api._
import play.api.mvc._
import play.api.data._
import org.apache.commons.codec.digest.DigestUtils

object GravatarService {

  val GRAVATAR:String = "http://www.gravatar.com/";
  val GRAVATAR_SSL:String = "https://secure.gravatar.com/";
    
  def url (email:String): String = {
    val url = new StringBuilder().append(GRAVATAR)
		 println("Getting md5 url: "+url.toString)
        if (email.isEmpty) {
          //throw some exception?
        }

//        if(args.containsKey("secure") && args.get("secure") == Boolean.TRUE) {
//            url.append(GRAVATAR_SSL);
//            args.remove("secure");
//        }
//        else
//        url.+(GRAVATAR);

        def emailTrimmed = email.toLowerCase().trim();
        url.append("avatar/");
		 println("Getting md5 url: "+url )
        url.append(DigestUtils.md5Hex(email));
        println(url)
        return url.toString
//        if(!args.isEmpty()) {
//            List<String> params = new ArrayList<String>();
//
//            if(args.containsKey("size")) {
//                params.add("s="+args.get("size"));
//            }
//
//            if(args.containsKey("default")) {
//                params.add("d="+args.get("default"));
//            }
//
//            if(args.containsKey("rating")) {
//                params.add("r="+args.get("rating"));
//            }
//
//            url.append("?");
//
//            Iterator i = params.iterator();
//            while(i.hasNext()) {
//                url.append(i.next());
//                if(i.hasNext())
//                    url.append("&");
//            }
//        }
//
//        out.print(url.toString());
//
    }

}