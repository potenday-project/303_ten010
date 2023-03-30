import { Router } from 'express'
import asyncify from 'express-asyncify'
import { Configuration, OpenAIApi } from "openai";
import axios from 'axios'

import validateJwt from '../middlewares/jwt.js'

const router = asyncify(Router())

const type = {
    1: "로 3줄 이내 글을 써줘",
    2: "로 3줄 이내 시를 써줘",
    3: "로 3줄 이내 감상평을 써줘",
}

router.post('/', validateJwt, async (req, res) => {
    const configuration = new Configuration({
        organization: process.env.OPENAI_ORGANIZATION_ID,
        apiKey: process.env.OPENAI_API_KEY,
    });
    const openai = new OpenAIApi(configuration);

    const url = 'https://api.imagga.com/v2/tags?image_url=' + encodeURIComponent(req.body.photoUrl) + '&limit=5&language=ko';
    let {data:tags} = await axios.get(url, {
        headers: {
            Authorization: `Basic ${process.env.IMAAGA_ACCESS_KEY}`
        }
    })
    tags = tags.result.tags.map(t => t.tag.ko)

    const result = await openai.createChatCompletion({
        "model": "gpt-3.5-turbo",
        // "messages": [{"role": "user", "content": `write a poem with ${JSON.stringify(tags)}`}],
        "messages": [{"role": "user", "content": `${JSON.stringify(tags)} ${type[req.body.type]}`}],
        // "max_tokens": 80,
    }, undefined)
    res.json({text: result.data.choices[0].message.content} )
})

export default router
