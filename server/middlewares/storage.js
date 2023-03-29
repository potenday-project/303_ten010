import AWS from "aws-sdk";
import multer from 'multer'
import multerS3 from 'multer-s3'
import path from 'path'
import { v4 } from 'uuid';

const endpoint = new AWS.Endpoint('https://kr.object.ncloudstorage.com');
const region = 'kr-standard';
const access_key = process.env.NCP_ACCESS_KEY
const secret_key = process.env.NCP_SECRET_KEY

const s3 = new AWS.S3({
    endpoint: endpoint,
    region: region,
    credentials: {
        accessKeyId : access_key,
        secretAccessKey: secret_key
    }
});

export default multer({
    storage: multerS3({
        s3: s3,
        bucket: "sara",
        key: function (req, file, cb) {
            let extension = path.extname(file.originalname);
            cb(null, v4() + extension)
        },
        acl: 'public-read-write',
    })
})
